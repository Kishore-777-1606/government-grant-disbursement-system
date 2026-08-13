import api from "./api";

// Logs the user in and returns { userId, username, fullName, role } on success.
// On bad credentials the backend responds 404, on a disabled account 400 —
// both carry a { message } body which the caller (Login.jsx) surfaces to the user.
export const login = async (username, password) => {
    const response = await api.post("/api/auth/login", { username, password });
    return response.data;
};