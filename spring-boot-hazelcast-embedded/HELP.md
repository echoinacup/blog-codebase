# Getting Started

Clone registry app from here

Hazelcast embedded example is using older eureka one plugin and eureka two with
fix I was able to found.

both are working, so up to you which to use.
POM contains commented out eureka one and CacheConfig.java file contains beans to resolve issue for Eureka Two plugin.


Building docker image 
mvn spring-boot:build-image
gradle bootBuildImage


NOTE it can take some time for nodes to sync