// auction-web/src/main/java/com/jiat/auction/rest/AuctionApplication.java
package com.jiat.auction.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/rest")
public class AuctionApplication extends Application {
    // This class can be empty - it just registers the REST application
    // All @Path annotated classes in the same package or subpackages will be automatically discovered
}