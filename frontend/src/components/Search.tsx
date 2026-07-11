import searchIcon from "@/assets/search-white.png";

const Search: React.FC = () => {
  return (
    <div className="relative grow max-w-84">
      <input
        type="text"
        placeholder="Search"
        className="border py-2 px-4 rounded-full w-full hover:not-focus:outline"
      />
      <img
        src={searchIcon}
        className="size-7 absolute top-1.5 right-4 opacity-70 hover:opacity-100 cursor-pointer"
        alt="Search icon"
      />
    </div>
  );
};

export default Search;
