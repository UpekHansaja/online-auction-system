# Distributed Online Auction System

Here's a complete guide to all the endpoints and how to interact with your Online Auction System:

## 1. **Create a New Auction**
**Method**: POST  
**URL**: `/auction/rest/auctions`  
**Request Body** (JSON):
```json
{
    "id": "auction1",
    "title": "Rare Painting",
    "description": "19th century masterpiece",
    "startingPrice": 1000.0,
    "currentBid": 1000.0,
    "endTime": "2025-06-30T23:59:59",
    "active": true
}
```
**cURL Example**:
```bash
curl -X POST -H "Content-Type: application/json" -d '{
    "id": "auction1",
    "title": "Rare Painting",
    "description": "19th century masterpiece",
    "startingPrice": 1000.0,
    "currentBid": 1000.0,
    "endTime": "2025-06-30T23:59:59",
    "active": true
}' http://localhost:8080/auction/rest/auctions
```

## 2. **Place a Bid for an Auction**
**Method**: POST  
**URL**: `/auction/rest/auctions/{auctionId}/bids`  
**Request Body** (JSON):
```json
{
    "id": "bid1",
    "userId": "user123",
    "amount": 1200.0,
    "bidTime": "2025-06-02T14:30:00"
}
```
**cURL Example**:
```bash
curl -X POST -H "Content-Type: application/json" -d '{
    "id": "bid1",
    "userId": "user123",
    "amount": 1200.0,
    "bidTime": "2025-06-02T14:30:00"
}' http://localhost:8080/auction/rest/auctions/auction1/bids
```

## 3. **View All Auctions**
**Method**: GET  
**URL**: `/auction/rest/auctions`  
**Response** (JSON):
```json
[
    {
        "id": "auction1",
        "title": "Rare Painting",
        "description": "19th century masterpiece",
        "startingPrice": 1000.0,
        "currentBid": 1200.0,
        "endTime": "2025-06-30T23:59:59",
        "active": true
    },
    {
        "id": "auction2",
        "title": "Vintage Watch",
        "description": "Rolex 1950",
        "startingPrice": 5000.0,
        "currentBid": 5500.0,
        "endTime": "2025-06-15T12:00:00",
        "active": true
    }
]
```
**cURL Example**:
```bash
curl http://localhost:8080/auction/rest/auctions
```

## 4. **View All Bids for an Auction**
**Method**: GET  
**URL**: `/auction/rest/auctions/{auctionId}/bids`  
**Response** (JSON):
```json
[
    {
        "id": "bid1",
        "auctionId": "auction1",
        "userId": "user123",
        "amount": 1100.0,
        "bidTime": "2025-06-01T10:15:00"
    },
    {
        "id": "bid2",
        "auctionId": "auction1",
        "userId": "user456",
        "amount": 1200.0,
        "bidTime": "2025-06-01T11:30:00"
    }
]
```
**cURL Example**:
```bash
curl http://localhost:8080/auction/rest/auctions/auction1/bids
```


## Web Interface URLs

1. **Home Page**: `http://localhost:8080/auction/`
2. **View All Auctions**: `http://localhost:8080/auction/auctions`
3. **View Single Auction**: `http://localhost:8080/auction/auctions/{auctionId}`
   - Example: `http://localhost:8080/auction/auctions/auction1`

## Important Notes:

1. All REST endpoints return JSON responses
2. The countdown timer on the auction detail page updates in real-time
3. Replace `{auctionId}` with actual auction IDs in the URLs

