// auction-web/src/main/java/com/jiat/auction/rest/AuctionResource.java
package com.jiat.auction.rest;

import com.jiat.auction.ejb.AuctionService;
import com.jiat.auction.model.Auction;
import com.jiat.auction.model.Bid;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Stateless
@Path("/auctions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuctionResource {

    // Option 1: Simple injection without explicit lookup
    @EJB
    private AuctionService auctionService;

    // Option 2: If you need explicit lookup, use the correct JNDI name
    // @EJB(lookup = "java:global/auction-ear-1.0.0/auction-ejb-1.0.0/AuctionServiceImpl")
    // private AuctionService auctionService;

    @GET
    public List<Auction> getActiveAuctions() {
        return auctionService.getActiveAuctions();
    }

    @POST
    @Path("/{auctionId}/bids")
    public Response placeBid(@PathParam("auctionId") String auctionId, Bid bid) {
        bid.setAuctionId(auctionId);
        auctionService.placeBid(bid);
        return Response.ok().build();
    }

    @POST
    public Response createAuction(Auction auction) {
        auctionService.createAuction(auction);
        return Response.status(Response.Status.CREATED).build();
    }
}