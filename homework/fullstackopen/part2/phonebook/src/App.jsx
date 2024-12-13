import { useState, useEffect } from "react";
import AddContact from "./AddContacts";
import ShowContacts from "./ShowContacts";
import Filter from "./Filter";
import repository from "./ContactRepository";
import notification from "./Notification";

const App = () => {
  const [persons, setPersons] = useState([]);
  const [searchName, setSearchName] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    repository.listContacts().then((data) => setPersons(data));
  }, []);

  const setMessage = (message, isError = false) => {
    setSuccessMessage(isError ? "" : message);
    setErrorMessage(!isError ? "" : message);
    setTimeout(() => setSuccessMessage(""), 5000);
  };

  return (
    <div>
      <h2>Phonebook</h2>
      <notification.SuccessMessage message={successMessage} />
      <notification.ErrorMessage message={errorMessage} />
      <Filter searchName={searchName} setSearchName={setSearchName} />
      <AddContact
        persons={persons}
        setPersons={setPersons}
        setMessage={setMessage}
      />
      <ShowContacts
        persons={persons}
        nameFilter={searchName}
        setPersons={setPersons}
        setMessage={setMessage}
      />
    </div>
  );
};

export default App;
