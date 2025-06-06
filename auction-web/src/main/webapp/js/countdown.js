document.addEventListener('DOMContentLoaded', function () {

    const timeElements = document.querySelectorAll('[data-end]');

    timeElements.forEach(element => {
        const endTime = new Date(element.dataset.end).getTime();
        updateCountdown(element, endTime);

        setInterval(() => {
            updateCountdown(element, endTime);
        }, 1000);
    });
});

function updateCountdown(element, endTime) {
    const now = new Date().getTime();
    const distance = endTime - now;

    if (distance < 0) {
        element.textContent = "Auction Ended";
        return;
    }

    // Calculate time remaining.
    const days = Math.floor(distance / (1000 * 60 * 60 * 24));
    const hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
    const seconds = Math.floor((distance % (1000 * 60)) / 1000);

    element.textContent = `${days}d ${hours}h ${minutes}m ${seconds}s`;
}