import axios from "axios";

const api = axios.create({
    baseURL: "http://localhost:8080",
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

export default api;