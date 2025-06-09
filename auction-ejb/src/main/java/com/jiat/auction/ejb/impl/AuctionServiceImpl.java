package com.jiat.auction.ejb.impl;

import com.jiat.auction.ejb.AuctionService;
import com.jiat.auction.model.Auction;
import com.jiat.auction.model.Bid;
import jakarta.annotation.Resource;
import jakarta.ejb.*;
import jakarta.jms.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@Local(AuctionService.class)
public class AuctionServiceImpl implements AuctionService {

    private final ConcurrentMap<String, Auction> auctions = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, List<Bid>> auctionBids = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, Bid> highestBids = new ConcurrentHashMap<>();

    @Resource(lookup = "jms/AuctionConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(lookup = "jms/AuctionQueue")
    private Queue auctionQueue;

    @Override
    public void createAuction(Auction auction) {
        auctions.put(auction.getId(), auction);
        auctionBids.put(auction.getId(), new ArrayList<>());
    }

    @Override
    public List<Auction> getActiveAuctions() {
        return new ArrayList<>(auctions.values());
    }

    @Override
    public synchronized void placeBid(Bid bid) {
        Auction auction = auctions.get(bid.getAuctionId());
        if (auction != null && auction.isActive()) {
            if (bid.getAmount() > auction.getCurrentBid()) {

                auction.setCurrentBid(bid.getAmount());

                List<Bid> bids = auctionBids.get(bid.getAuctionId());
                if (bids != null) {
                    bids.add(bid);
                }

                highestBids.put(bid.getAuctionId(), bid);

                sendBidNotification(bid);

                System.out.println("Bid placed successfully: $" + bid.getAmount() +
                        " for auction: " + bid.getAuctionId() +
                        " by user: " + bid.getUserId());
            } else {
                System.out.println("Bid rejected - amount too low: $" + bid.getAmount() +
                        " (current: $" + auction.getCurrentBid() + ")");
            }
        } else {
            System.out.println("Bid rejected - auction not found or inactive: " + bid.getAuctionId());
        }
    }

    @Override
    public Auction getAuctionById(String auctionId) {
        return auctions.get(auctionId);
    }

    @Override
    public List<Bid> getBidsForAuction(String auctionId) {
        List<Bid> bids = auctionBids.get(auctionId);
        return bids != null ? new ArrayList<>(bids) : new ArrayList<>();
    }

    @Override
    public Bid getHighestBidForAuction(String auctionId) {
        return highestBids.get(auctionId);
    }

    @Override
    public Map<String, Object> getTimeRemaining(String auctionId) {
        Auction auction = auctions.get(auctionId);
        Map<String, Object> result = new HashMap<>();
        if (auction != null) {
            long seconds = ChronoUnit.SECONDS.between(
                    LocalDateTime.now(),
                    auction.getEndTime()
            );

            if (seconds < 0) {
                seconds = 0;
                auction.setActive(false);
            }

            // format seconds into days/hours/minutes/seconds
            long days = seconds / 86400;
            seconds %= 86400;
            long hours = seconds / 3600;
            seconds %= 3600;
            long minutes = seconds / 60;
            seconds %= 60;

            result.put("days", days);
            result.put("hours", hours);
            result.put("minutes", minutes);
            result.put("seconds", seconds);
            result.put("expired", days == 0 && hours == 0 && minutes == 0 && seconds == 0);
        }
        return result;
    }

    private void sendBidNotification(Bid bid) {
        try (Connection connection = connectionFactory.createConnection();
             Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
             MessageProducer producer = session.createProducer(auctionQueue)) {

            ObjectMessage message = session.createObjectMessage(bid);
            producer.send(message);

            System.out.println("Bid notification sent to JMS queue for auction: " + bid.getAuctionId());

        } catch (JMSException e) {
            System.err.println("Error sending bid notification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Object> getBidStatistics(String auctionId) {
        Map<String, Object> stats = new HashMap<>();
        List<Bid> bids = auctionBids.get(auctionId);

        if (bids != null && !bids.isEmpty()) {
            stats.put("totalBids", bids.size());
            stats.put("uniqueBidders", bids.stream().map(Bid::getUserId).distinct().count());
            stats.put("highestBid", highestBids.get(auctionId));
        } else {
            stats.put("totalBids", 0);
            stats.put("uniqueBidders", 0);
            stats.put("highestBid", null);
        }

        return stats;
    }
}