// Tracking script to run on page load
(function () {
    function extractTrackingData(page) {
        let payload = { weight: null, productId: null, quantity: null };

        if (page.startsWith("/detail/")) {
            // Extract productId from detail page URL
            payload.productId = page.split("/detail/")[1];
            payload.weight = 1;
            payload.quantity = 1;
        } else if (page.startsWith("/shopping-cart")) {
            // Extract query parameters from cart page URL
            const urlParams = new URLSearchParams(page.split("?")[1]);
            payload.productId = urlParams.get("addProduct");
            payload.quantity = urlParams.has("quantity") ? parseInt(urlParams.get("quantity"), 10) : 1;
            payload.weight = 2;
        }

        return payload;
    }

    function sendTrackingData(payload) {
        if (!payload) return;

        fetch("/api/track", {
            method: "post",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        })
        .then(response => {
            if (!response.ok) {
                console.error("Failed to track user behavior:", response.statusText);
            }
        })
        .catch(error => console.error("Error sending tracking data:", error));
    }

    // Extract current page and send tracking data
    const currentPage = window.location.pathname + window.location.search;
    const trackingData = extractTrackingData(currentPage);
    if(trackingData.productId){
        sendTrackingData(trackingData)
    }
})();
