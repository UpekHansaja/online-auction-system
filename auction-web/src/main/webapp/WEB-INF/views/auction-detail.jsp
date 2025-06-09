<%@include file="includes/header.jsp" %>
<%@page import="com.jiat.auction.model.Auction" %>
<%@ page import="com.jiat.auction.ejb.AuctionService" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.jiat.auction.ejb.impl.AuctionServiceImpl" %>
<%
    Auction auction = (Auction) request.getAttribute("auction");
    String currentUserId = request.getSession().getId();
%>

<div class="auction-container" data-auction-id="<%= auction.getId() %>" data-user-id="<%= currentUserId %>">
    <h2><%= auction.getTitle() %>
    </h2>
    <div class="auction-detail">
        <p class="description"><%= auction.getDescription() %>
        </p>
        <div class="auction-meta">
            <div class="current-bid">
                <h3>Current Bid: $<span id="currentBid"><%= auction.getCurrentBid() %></span></h3>
                <p id="bidStatus" class="bid-status" style="display: none;"></p>
            </div>
            <div class="time-remaining">
                <h3>Time Remaining: <span id="countdown" data-end="<%= auction.getEndTime() %>"></span></h3>
            </div>
            <div class="connection-status">
                <p>
                    <span id="connectionStatus" class="status-indicator">Connecting...</span>
                </p>
                <p>
                    <span id="clientCount" class="client-count">
<%--                        <%= request.getAttribute("viewCount") != null ? request.getAttribute("viewCount") : 0 %>--%>
                        <%
                            Map<String, Object> uniqueBidders = new AuctionServiceImpl().getBidStatistics(auction.getId());
                            int clientCount = uniqueBidders != null ? uniqueBidders.size() : 0;
                        %>
                        <%= clientCount %>
                    </span>
                    <span id="" class="client-count">&nbsp;&nbsp; People Watching this</span>
                </p>
            </div>
        </div>

        <form id="bidForm" action="${pageContext.request.contextPath}/bids" method="post">
            <input type="hidden" name="auctionId" value="<%= auction.getId() %>">
            <input type="hidden" name="userId" value="<%= currentUserId %>">
            <div class="bid-form">
                <label for="amount">Your Bid:</label>
                <input type="number" id="amount" name="amount"
                       min="<%= auction.getCurrentBid() + 1 %>"
                       step="0.01" required>
                <button type="submit" id="bidButton">Place Bid</button>
            </div>
            <div class="bid-help">
                <small>Minimum bid: $<span id="minBid"><%= auction.getCurrentBid() + 1 %></span></small>
            </div>
        </form>
    </div>
</div>

<style>
    .auction-container {
        max-width: 800px;
        margin: 0 auto;
        padding: 20px;
    }

    .auction-detail {
        background: white;
        border-radius: 8px;
        padding: 20px;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    }

    .auction-meta {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 20px;
        margin: 20px 0;
    }

    .current-bid, .time-remaining {
        padding: 15px;
        background: #f8f9fa;
        border-radius: 6px;
        text-align: center;
    }

    .current-bid h3 {
        color: #2e7d32;
        margin: 0 0 10px 0;
    }

    .time-remaining h3 {
        color: #1976d2;
        margin: 0;
    }

    .bid-status {
        font-size: 14px;
        font-weight: bold;
        margin-top: 5px;
        padding: 5px 10px;
        border-radius: 4px;
        transition: all 0.3s ease;
    }

    .your-bid {
        color: #2e7d32;
        background-color: #e8f5e8;
        border: 1px solid #4caf50;
    }

    .other-bid {
        color: #d32f2f;
        background-color: #ffebee;
        border: 1px solid #f44336;
        animation: shake 0.5s ease-in-out;
    }

    @keyframes shake {
        0%, 100% {
            transform: translateX(0);
        }
        25% {
            transform: translateX(-5px);
        }
        75% {
            transform: translateX(5px);
        }
    }

    .connection-status {
        grid-column: 1 / -1;
        text-align: center;
        padding: 10px;
    }

    .status-indicator {
        padding: 6px 12px;
        border-radius: 20px;
        font-size: 12px;
        font-weight: bold;
        display: inline-block;
        margin-right: 10px;
    }

    .status-connected {
        background-color: #e8f5e8;
        color: #2e7d32;
    }

    .status-disconnected {
        background-color: #ffebee;
        color: #d32f2f;
    }

    .status-connecting {
        background-color: #fff3e0;
        color: #f57c00;
    }

    .client-count {
        font-size: 11px;
        color: #666;
        background: #f0f0f0;
        padding: 2px 8px;
        border-radius: 10px;
    }

    .bid-form {
        display: flex;
        gap: 10px;
        align-items: center;
        margin-top: 20px;
        padding: 20px;
        background: #f8f9fa;
        border-radius: 6px;
    }

    .bid-form label {
        font-weight: bold;
        color: #333;
    }

    .bid-form input[type="number"] {
        flex: 1;
        padding: 10px;
        border: 2px solid #ddd;
        border-radius: 4px;
        font-size: 16px;
    }

    .bid-form input[type="number"]:focus {
        border-color: #4caf50;
        outline: none;
    }

    .bid-form button {
        padding: 10px 20px;
        background: #4caf50;
        color: white;
        border: none;
        border-radius: 4px;
        font-size: 16px;
        font-weight: bold;
        cursor: pointer;
        transition: background 0.3s ease;
    }

    .bid-form button:hover:not(:disabled) {
        background: #45a049;
    }

    .bid-form button:disabled {
        background: #ccc;
        cursor: not-allowed;
    }

    .bid-help {
        text-align: center;
        margin-top: 10px;
    }

    .bid-help small {
        color: #666;
    }

    .auction-ended-message h3 {
        color: #d32f2f !important;
        margin: 0 0 15px 0;
    }

    @media (max-width: 768px) {
        .auction-meta {
            grid-template-columns: 1fr;
        }

        .bid-form {
            flex-direction: column;
            align-items: stretch;
        }

        .bid-form input[type="number"] {
            margin: 10px 0;
        }
    }

    /* Pulse animation for bid updates */
    @keyframes pulse {
        0% {
            transform: scale(1);
        }
        50% {
            transform: scale(1.05);
        }
        100% {
            transform: scale(1);
        }
    }

    .bid-update-animation {
        animation: pulse 0.5s ease-in-out;
    }
</style>

<script src="${pageContext.request.contextPath}/js/realtime.js"></script>
<script>
    // Set context path for the WebSocket client
    window.contextPath = '<%= request.getContextPath() %>';

    // Initialize WebSocket with custom options
    const wsOptions = {
        maxReconnectAttempts: 10,
        reconnectDelay: 2000,
        heartbeatInterval: 30000,

        onBidUpdate: function (data) {
            // Custom bid update handler
            console.log('Bid update received:', data);

            // Update current bid
            const currentBidElement = document.getElementById('currentBid');
            if (currentBidElement) {
                currentBidElement.textContent = data.amount.toFixed(2);
                currentBidElement.classList.add('bid-update-animation');
                setTimeout(() => {
                    currentBidElement.classList.remove('bid-update-animation');
                }, 500);
            }

            // Update minimum bid
            const amountInput = document.getElementById('amount');
            const minBidSpan = document.getElementById('minBid');
            if (amountInput && minBidSpan) {
                const newMin = (data.amount + 0.01).toFixed(2);
                amountInput.min = newMin;
                minBidSpan.textContent = newMin;
            }

            // Show bid status
            const bidStatusElement = document.getElementById('bidStatus');
            if (bidStatusElement) {
                const isYourBid = data.userId === '<%= currentUserId %>';

                if (isYourBid) {
                    bidStatusElement.textContent = 'Your Bid';
                    bidStatusElement.className = 'bid-status your-bid';
                } else {
                    bidStatusElement.textContent = 'Outbid!';
                    bidStatusElement.className = 'bid-status other-bid';

                    // Auto-hide after 5 seconds
                    setTimeout(() => {
                        bidStatusElement.style.display = 'none';
                    }, 5000);
                }

                bidStatusElement.style.display = 'block';
            }

            // Play notification sound for outbid (optional)
            if (data.userId !== '<%= currentUserId %>') {
                playNotificationSound();
            }
        },

        onConnectionChange: function (status, data) {
            const statusElement = document.getElementById('connectionStatus');
            const clientCountElement = document.getElementById('clientCount');

            if (statusElement) {
                let statusText = '';
                let className = 'status-indicator';

                switch (status) {
                    case 'connecting':
                        statusText = 'Connecting...';
                        className += ' status-connecting';
                        break;
                    case 'connected':
                        statusText = 'Live';
                        className += ' status-connected';
                        break;
                    case 'disconnected':
                        statusText = 'Offline';
                        className += ' status-disconnected';
                        break;
                    case 'reconnecting':
                        statusText = `Reconnecting... (${data.attempt}/${data.maxAttempts})`;
                        className += ' status-connecting';
                        break;
                    case 'error':
                        statusText = 'Error';
                        className += ' status-disconnected';
                        break;
                }

                statusElement.textContent = statusText;
                statusElement.className = className;
                document.addEventListener('clientCountUpdate', function (e) {
                    if (clientCountElement && typeof e.detail.count === 'number') {
                        console.log('Client count:', clientCountElement.textContent);
                        clientCountElement.textContent = e.detail.count;
                    }
                });
            }
        }
    };

    // Initialize WebSocket
    const auctionWebSocket = initAuctionWebSocket(
        '<%= auction.getId() %>',
        '<%= currentUserId %>',
        wsOptions
    );

    // Enhanced form submission
    document.getElementById('bidForm').addEventListener('submit', function (e) {
        const bidButton = document.getElementById('bidButton');
        const amountInput = document.getElementById('amount');

        // Validate bid amount
        const currentBid = parseFloat(document.getElementById('currentBid').textContent);
        const bidAmount = parseFloat(amountInput.value);

        if (bidAmount <= currentBid) {
            e.preventDefault();
            alert('Your bid must be higher than the current bid!');
            return;
        }

        // Disable button to prevent double submission
        bidButton.disabled = true;
        bidButton.textContent = 'Placing Bid...';

        // Re-enable button after submission
        setTimeout(() => {
            bidButton.disabled = false;
            bidButton.textContent = 'Place Bid';
        }, 3000);
    });

    // Optional: Play notification sound
    function playNotificationSound() {
        // Create a simple beep sound
        const audioContext = new (window.AudioContext || window.webkitAudioContext)();
        const oscillator = audioContext.createOscillator();
        const gainNode = audioContext.createGain();

        oscillator.connect(gainNode);
        gainNode.connect(audioContext.destination);

        oscillator.frequency.value = 800;
        oscillator.type = 'sine';

        gainNode.gain.setValueAtTime(0.3, audioContext.currentTime);
        gainNode.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + 0.5);

        oscillator.start(audioContext.currentTime);
        oscillator.stop(audioContext.currentTime + 0.5);
    }

    // Update countdown timer
    function updateCountdown() {
        const countdownElement = document.getElementById('countdown');
        if (countdownElement) {
            const endTime = new Date(countdownElement.getAttribute('data-end'));
            const now = new Date();
            const timeDiff = endTime - now;

            if (timeDiff > 0) {
                const days = Math.floor(timeDiff / (1000 * 60 * 60 * 24));
                const hours = Math.floor((timeDiff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
                const minutes = Math.floor((timeDiff % (1000 * 60 * 60)) / (1000 * 60));
                const seconds = Math.floor((timeDiff % (1000 * 60)) / 1000);

                countdownElement.textContent = `${days}d ${hours}h ${minutes}m ${seconds}s`;
            } else {
                countdownElement.textContent = 'Auction Ended';
                document.querySelectorAll('.time-remaining').forEach(el => el.className += ' auction-ended-message');
                document.getElementById('bidButton').disabled = true;
            }
        }
    }

    // Update countdown every second
    setInterval(updateCountdown, 1000);
    updateCountdown(); // Initial call
</script>

<%@include file="includes/footer.jsp" %>