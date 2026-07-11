import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter } from "react-router-dom";
import AuthContextProvider from "@/context/AuthContextProvider.tsx";
import { MusicPlayerProvider } from "./context/MusicPlayerContextProvider.tsx";
import PlaylistContextProvider from "./context/PlaylistContextProvider.tsx";
import "./index.css";
import App from "./App.tsx";

const queryClient = new QueryClient();

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <BrowserRouter>
      <AuthContextProvider>
        <PlaylistContextProvider>
          <QueryClientProvider client={queryClient}>
            <MusicPlayerProvider>
              <App />
            </MusicPlayerProvider>
          </QueryClientProvider>
        </PlaylistContextProvider>
      </AuthContextProvider>
    </BrowserRouter>
  </StrictMode>,
);
