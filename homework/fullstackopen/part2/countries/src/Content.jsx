/* eslint-disable react/prop-types */

const Content = ({ countries, setFilterVal }) => {
  if (countries.length == 0) return <div>No countries found.</div>;

  if (countries.length > 10)
    return <div>Too many matches, specify another filter</div>;

  if (countries.length > 1) {
    return (
      <div>
        {countries.map((x) => (
          <div key={x.name.official}>
            {x.name.common}
            <button onClick={() => setFilterVal(x.name.common)}>show</button>
          </div>
        ))}
      </div>
    );
  }

  const country = countries[0];
  const languages = Object.values(country.languages);
  return (
    <div>
      <h3>{country.name.common}</h3>
      <div>capital {country.capital[0]}</div>
      <div>area {country.area}</div>
      <h4>languages:</h4>
      <table>
        <tbody>
          <ul>
            {languages.map((x) => (
              <li key={x}>{x}</li>
            ))}
          </ul>
        </tbody>
      </table>
      <img src={country.flags.png} alt="country flag" />
    </div>
  );
};

export default Content;
