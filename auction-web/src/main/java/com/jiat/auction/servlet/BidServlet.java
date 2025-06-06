package com.jiat.auction.servlet;

import com.jiat.auction.ejb.AuctionService;
import com.jiat.auction.model.Bid;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet("/bids")
public class BidServlet extends HttpServlet {

//    @EJB(lookup = "java:global/auction-ear/auction-ejb/AuctionServiceImpl!com.jiat.auction.ejb.AuctionService")
    @EJB
    private AuctionService auctionService;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String auctionId = request.getParameter("auctionId");
        String userId = request.getParameter("userId");
        double amount = Double.parseDouble(request.getParameter("amount"));

        Bid bid = new Bid();
        bid.setAuctionId(auctionId);
        bid.setUserId(userId);
        bid.setAmount(amount);
        bid.setBidTime(LocalDateTime.now());

        auctionService.placeBid(bid);

        response.sendRedirect(request.getContextPath() + "/auctions/" + auctionId);
    }
}