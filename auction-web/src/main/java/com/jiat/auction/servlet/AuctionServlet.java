package com.jiat.auction.servlet;

import com.jiat.auction.ejb.AuctionService;
import com.jiat.auction.model.Auction;
import jakarta.ejb.EJB;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "AuctionServlet", urlPatterns = {"/auctions", "/auctions/*"})
public class AuctionServlet extends HttpServlet {

//    @EJB(lookup = "java:global/auction-ear/auction-ejb/AuctionServiceImpl!com.jiat.auction.ejb.AuctionService")
    @EJB()
    private AuctionService auctionService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getRequestURI().substring(request.getContextPath().length());

        if ("/auctions".equals(path)) {
            // Obtain a List all Auctions
            List<Auction> auctions = auctionService.getActiveAuctions();
            request.setAttribute("auctions", auctions);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/auction-list.jsp");
            dispatcher.forward(request, response);

        } else if (path.startsWith("/auctions/")) {
            // Show a single auction
            String auctionId = path.substring("/auctions/".length());
            Auction auction = auctionService.getAuctionById(auctionId);
            int viewCount = 1; // Default view count
            if (auction != null) {
                request.setAttribute("auction", auction);
                // Increment view count according to active user sessions for current auction
                request.setAttribute("viewCount", viewCount + 1);
                RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/auction-detail.jsp");
                dispatcher.forward(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }
}