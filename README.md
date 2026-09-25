# Load Balancer Library

A generic, pluggable **client-side load balancer** for Spring Boot microservices. Supports 5 load balancing algorithms, switchable at runtime via configuration — no code changes required.

Built as a hands-on system design project: implementing core distributed-systems concepts (load balancing strategies, service discovery, centralized configuration, containerization) from scratch rather than just using off-the-shelf tools.

---

## Features

- **5 load balancing algorithms** — Round Robin, Weighted Round Robin, Least Connections, Random, IP Hash
- **Strategy + Factory pattern** — algorithm is selected purely via a property (`loadbalancer.algorithm`), no code changes to switch
- **Dynamic service discovery** via Netflix Eureka — no hardcoded instance lists
- **Centralized configuration** via Spring Cloud Config Server, backed by a Git repository
- **Thread-safe implementations** — `AtomicInteger`, `ConcurrentHashMap`, and `synchronized` used where correctness under concurrency actually requires it
- **Dockerized** — full stack (Eureka + Config Server + this library) runs with a single `docker-compose up`
- **Published to Docker Hub** — usable without building from source

---

##Architecture

config-repo (GitHub, properties only)
    |
    v
Config Server (:8888) → centralized properties →
    |
Eureka Server (:8761) ←→ Loadbalancer Library (:9090)
    |
    v
consuming services register/discover here

The library is deliberately not a standalone service — it's a dependency meant to be embedded inside any Spring Boot microservice that needs to pick one instance out of several running copies of another service.

---

## Algorithms

The library includes 5 algorithms, each suited to a different situation:

- **Round Robin** — when all servers have roughly equal capacity, cycles through them turn by turn
- **Weighted Round Robin** — when some servers are more powerful than others, gives them proportionally more traffic (smooth distribution, same idea Nginx uses)
- **Least Connections** — when requests take varying amounts of time to process, routes to whichever server currently has the fewest active requests
- **Random** — at large scale (50+ instances), skips tracking any state and just picks one at random
- **IP Hash** — routes the same client to the same server every time (needed for session/cart stickiness)

IP Hash has a known limitation: when the instance list changes, most client-to-server mappings shift (the classic "rehashing problem"). Production systems usually solve this with Consistent Hashing. A simpler version is implemented here — a deliberate scope tradeoff, not an oversight.

---

## Tech Stack

Java 17, Spring Boot 4.1.1, Spring Cloud Netflix Eureka, Spring Cloud Config Server, Maven, Lombok, Docker, Docker Compose

---

## Related Repositories

This library depends on two companion services, kept separate since they represent shared infrastructure rather than library-specific code:

- [eureka-server](https://github.com/sabaanamkhan-ship-it/eureka-server) — service registry
- [config-server](https://github.com/sabaanamkhan-ship-it/config-server) — centralized config server
- [config-repo](https://github.com/sabaanamkhan-ship-it/config-repo) — Git-backed property source for Config Server

---

## Running It

### Option A — Docker Compose (recommended)

git clone https://github.com/sabaanamkhan-ship-it/loadbalancer-library.git
cd loadbalancer-library
docker-compose up

This starts Eureka Server, Config Server, and the library in the correct order, using health-check-gated startup so each service only starts once its dependency is actually ready — not just once the container exists.

### Option B — Pull the published image directly

docker pull sabaanamkhanshipit/loadbalancer-library

### Verify it's working

- `http://localhost:8761` — Eureka dashboard, showing registered instances
- `http://localhost:8888/loadbalancer-library/default` — Config Server serving properties as JSON
- `http://localhost:9090/select-instance` — the load balancer's selected instance

### Switching algorithms

Edit `loadbalancer.algorithm` in config-repo's `loadbalancer-library.properties` (`round-robin` / `weighted-round-robin` / `least-connections` / `random` / `ip-hash`) and restart the app. No code changes needed.

---

## Usage in a consuming service

```java
@Autowired
private LoadBalancerFactory loadBalancerFactory;

@Autowired
private DiscoveryClient discoveryClient;

public void callDownstreamService() {
    List<ServiceInstance> eurekaInstances = discoveryClient.getInstances("ORDER-SERVICE");

    List<ServiceInstanceDto> instances = eurekaInstances.stream()
            .map(i -> ServiceInstanceDto.builder()
                    .id(i.getInstanceId()).host(i.getHost()).port(i.getPort()).build())
            .sorted(Comparator.comparing(ServiceInstanceDto::getId))
            .toList();

    LoadBalancer loadBalancer = loadBalancerFactory.getLoadBalancer();
    ServiceInstanceDto chosen = loadBalancer.selectInstance(instances, context);

    restTemplate.getForObject(chosen.getUrl() + "/api/orders", ...);
}
```

The library never hardcodes a target service name — the calling service decides which service's instances it needs.

---

## Design Decisions Worth Knowing

- **Builder pattern on ServiceInstanceDto** instead of a plain constructor — not every algorithm needs every field (weight is irrelevant to Round Robin, for example), so unused fields default sensibly instead of being forced on every caller.
- **IP Hash breaks the common interface signature** since it needs an extra clientIp parameter — handled via method overloading rather than forcing every algorithm to carry a context object it doesn't use.
- **Eureka's DiscoveryClient.getInstances() doesn't guarantee consistent ordering** across calls — this made Round Robin look broken until instances were explicitly sorted by ID before being handed to the load balancer.
- **Zero-downtime config refresh (via @RefreshScope + Actuator) was explored but not included** — it hit a Spring Boot 4.1.1 compatibility issue where Actuator endpoints consistently returned 404. Rather than fight a framework bug, the app currently requires a restart to pick up config changes; this is documented as a known, intentional tradeoff.

---

## What's Not Included (Yet)

- **Consistent Hashing** — IP Hash currently uses simple modulo hashing (`clientIp.hashCode() % instances.size()`). This means that whenever the instance list changes — a server is added or removed — most existing client-to-server mappings shift, even for clients whose IP never changed. This breaks session/cache stickiness right when it matters most (during a scaling event). Consistent Hashing (placing servers and clients on a hash ring) solves this by affecting only the clients nearest to the changed instance, leaving everyone else's mapping untouched.
- CI/CD pipeline
- Cloud deployment

---

## License

MIT
