import axios from "axios";

const api = axios.create({
    baseURL: "http://localhost:8080/api",
    headers: {
        "Content-Type": "application/json",
    },
});

// =====================================================
// REQUEST INTERCEPTOR
// =====================================================

api.interceptors.request.use(
    (config) => {
        const token = sessionStorage.getItem("authToken");

        console.log(
            "🔐 Axios request:",
            config.method?.toUpperCase(),
            config.url
        );

        console.log(
            "🔑 Token:",
            token ? "EXISTS" : "MISSING"
        );

        if (token) {
            config.headers = config.headers || {};

            config.headers.Authorization = `Bearer ${token}`;

            console.log(
                "✅ Authorization header added"
            );
        } else {
            console.log(
                "❌ No JWT token found in sessionStorage"
            );
        }

        return config;
    },

    (error) => {
        console.error(
            "❌ Request interceptor error:",
            error
        );

        return Promise.reject(error);
    }
);

// =====================================================
// RESPONSE INTERCEPTOR
// =====================================================

api.interceptors.response.use(
    (response) => {
        return response;
    },

    (error) => {
        console.error(
            "❌ API Error:",
            error.response?.status,
            error.config?.url
        );

        console.error(
            "Backend response:",
            error.response?.data
        );

        // Unauthorized
        if (error.response?.status === 401) {

            console.log(
                "🔒 Unauthorized - clearing authentication"
            );

            sessionStorage.removeItem("authToken");
            sessionStorage.removeItem("authRole");
            sessionStorage.removeItem("authUsername");

            if (
                window.location.pathname !== "/login"
            ) {
                window.location.href = "/login";
            }
        }

        return Promise.reject(error);
    }
);

export default api;