/* eslint-disable react/prop-types */

const Filter = ({ label, filterVal, setFilterVal }) => {
  return (
    <div>
      {label}
      <input
        value={filterVal}
        onChange={(event) => setFilterVal(event.target.value)}
      ></input>
    </div>
  );
};

export default Filter;
