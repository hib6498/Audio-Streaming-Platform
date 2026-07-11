import type { JSX } from "react";
import { Route, Routes } from "react-router-dom";

import Login from "./pages/auth/Login";
import Register from "./pages/auth/Register";
import Home from "./pages/home/Home";
import Upload from "./pages/upload/Upload";
import Profile from "./pages/profile/Profile";
import HeaderLayout from "./HeaderLayout";
import ArtistPage from "./pages/artist/ArtistPage";
import Playlist from "./pages/playlist/Playlist";

const App: () => JSX.Element = () => {
  return (
    <>
      <Routes>
        <Route path="/" element={<HeaderLayout />}>
          <Route index element={<Home />}></Route>
          <Route path="/profile" element={<Profile />}></Route>
          <Route path="/user/:id" element={<ArtistPage />}></Route>
          <Route path="/playlist/:id" element={<Playlist />}></Route>
        </Route>
        <Route path="/login" element={<Login />}></Route>
        <Route path="/register" element={<Register />}></Route>
        <Route path="/upload" element={<Upload />}></Route>
      </Routes>
    </>
  );
};

export default App;
