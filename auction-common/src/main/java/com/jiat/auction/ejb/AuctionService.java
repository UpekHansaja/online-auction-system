package com.jiat.auction.ejb;

import com.jiat.auction.model.Auction;
import com.jiat.auction.model.Bid;
import jakarta.ejb.Local;

import java.util.List;
import java.util.Map;

@Local
public interface AuctionService {

    void createAuction(Auction auction);

    List<Auction> getActiveAuctions();

    void placeBid(Bid bid);

    Auction getAuctionById(String auctionId);

    List<Bid> getBidsForAuction(String auctionId);

    Bid getHighestBidForAuction(String auctionId);

    Map<String, Object> getTimeRemaining(String auctionId);

}