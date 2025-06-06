<%@include file="includes/header.jsp" %>
<%@page import="com.jiat.auction.model.Auction" %>
<%
    Auction auction = (Auction) request.getAttribute("auction");
%>

<h2><%= auction.getTitle() %>
</h2>
<div class="auction-detail">
    <p class="description"><%= auction.getDescription() %>
    </p>
    <div class="auction-meta">
        <div class="current-bid">
            <h3>Current Bid: $<span id="currentBid"><%= auction.getCurrentBid() %></span></h3>
        </div>
        <div class="time-remaining">
            <h3>Time Remaining: <span id="countdown" data-end="<%= auction.getEndTime() %>"></span></h3>
        </div>
    </div>

    <form action="${pageContext.request.contextPath}/bids" method="post">
        <input type="hidden" name="auctionId" value="<%= auction.getId() %>">
        <input type="hidden" name="userId" value="<%= request.getSession().getId() %>">
        <div class="bid-form">
            <label for="amount">Your Bid:</label>
            <input type="number" id="amount" name="amount"
                   min="<%= auction.getCurrentBid() + 1 %>"
                   step="0.01" required>
            <button type="submit">Place Bid</button>
        </div>
    </form>
</div>

<script>
    // Auto-refresh the page every 10 seconds to get bid updates
    setTimeout(function () {
        window.location.reload();
    }, 10000);
</script>

<%@include file="includes/footer.jsp" %>