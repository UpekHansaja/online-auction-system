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

    @EJB
    private AuctionService auctionService;

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