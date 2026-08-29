import axios from "axios";

const api = axios.create({
   baseURL: "https://grant-disbursement-backend.onrender.com",
    headers: {
        "Content-Type": "application/json",
    },
});

// Attach the JWT token (if we have one) to every outgoing request.
api.interceptors.request.use((config) => {
    const stored = localStorage.getItem("user");
    if (stored) {
        const user = JSON.parse(stored);
        if (user?.token) {
            config.headers.Authorization = `Bearer ${user.token}`;
        }
    }
    return config;
});

// If the token is missing/expired/invalid, the backend returns 401.
// Clear the stale session and send the user back to login instead of
// leaving them on a page where every API call silently fails.
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            localStorage.removeItem("user");
            window.location.href = "/";
        }
        return Promise.reject(error);
    }
);

export default api;