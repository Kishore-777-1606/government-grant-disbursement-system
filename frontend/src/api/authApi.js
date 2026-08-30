import axiosInstance from "./axiosInstance";

export const loginUser = async (username, password) => {
  try {
           const response = await axiosInstance.post("/auth/login", {
      username,
      password,
    });

    return response.data;
  } catch (error) {
    if (error.response) {
      throw new Error(
        error.response.data?.message ||
          "Invalid username or password",
        {
          cause: error,
        }
      );
    }

    if (error.request) {
      throw new Error(
        "Unable to connect to the server. Please check the backend.",
        {
          cause: error,
        }
      );
    }

    throw new Error(
      "Login failed. Please try again.",
      {
        cause: error,
      }
    );
  }
};

export const logoutUser = () => {
  sessionStorage.removeItem("authToken");
  sessionStorage.removeItem("authRole");
  sessionStorage.removeItem("authUsername");
};