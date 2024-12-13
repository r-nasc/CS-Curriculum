/* eslint-disable react/prop-types */

const Filter = ({ searchName, setSearchName }) => {
  const changeSearchName = (event) => {
    setSearchName(event.target.value);
  };

  return (
    <div>
      filter shown with
      <input value={searchName} onChange={changeSearchName} />
    </div>
  );
};

export default Filter;
