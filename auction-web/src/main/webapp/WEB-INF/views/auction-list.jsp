<%@include file="includes/header.jsp" %>
<%@page import="java.util.List" %>
<%@page import="com.jiat.auction.model.Auction" %>
<%
    List<Auction> auctions = (List<Auction>) request.getAttribute("auctions");
%>

<h2>Active Auctions</h2>
<div class="auction-grid">
    <% for (Auction auction : auctions) { %>
    <div class="auction-card">
        <h3><a href="${pageContext.request.contextPath}/auctions/<%= auction.getId() %>"><%= auction.getTitle() %>
        </a></h3>
        <p><%= auction.getDescription() %>
        </p>
        <div class="auction-info">
            <span class="current-bid">Current Bid: $<%= auction.getCurrentBid() %></span>
            <span class="time-left" data-end="<%= auction.getEndTime() %>"></span>
        </div>
    </div>
    <% } %>
</div>
<%@include file="includes/footer.jsp" %>