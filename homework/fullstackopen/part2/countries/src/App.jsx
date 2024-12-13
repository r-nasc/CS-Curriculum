import { useState, useEffect } from "react";
import countriesRepository from "./CountriesRepository";
import Filter from "./Filter";
import Content from "./Content";

const caseInsensitiveInclude = (a, b) =>
  a.toLowerCase().includes(b.toLowerCase());

function App() {
  const [countryFilter, setCountryFilter] = useState("");
  const [countries, setCountries] = useState([]);

  useEffect(() => {
    countriesRepository.listCountries().then((resp) => setCountries(resp));
  }, []);

  const filteredCountries = countries.filter((x) =>
    caseInsensitiveInclude(x.name.common, countryFilter)
  );

  return (
    <>
      <Filter
        label="Find countries"
        filterVal={countryFilter}
        setFilterVal={setCountryFilter}
      />
      <Content countries={filteredCountries} setFilterVal={setCountryFilter} />
    </>
  );
}

export default App;
