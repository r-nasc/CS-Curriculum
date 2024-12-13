/* eslint-disable react/prop-types */
import repository from "./ContactRepository";

const ShowContacts = ({ persons, setPersons, setMessage, nameFilter }) => {
  const filterName = (x) =>
    x.name.toLowerCase().includes(nameFilter.toLowerCase());

  return (
    <>
      <h2>Numbers</h2>
      <div>
        {persons.filter(filterName).map((person) => (
          <ContactListing
            key={person.id}
            person={person}
            persons={persons}
            setPersons={setPersons}
            setMessage={setMessage}
          />
        ))}
      </div>
    </>
  );
};

const ContactListing = ({ person, persons, setPersons, setMessage }) => {
  const deleteFunc = (event) => {
    if (window.confirm(`Delete ${person.name}?`)) {
      console.log(person.id, event);
      repository
        .deleteContact(person.id)
        .then((data) => {
          setPersons(persons.filter((x) => x.id != data.id));
          setMessage(`Deleted ${data.name}`);
        })
        .catch(() =>
          setMessage(
            "Failed to Delete Contact. Contact was already deleted!",
            true
          )
        );
    }
  };

  return (
    <div>
      {person.name} {person.number}
      <button onClick={deleteFunc}>delete</button>
    </div>
  );
};

export default ShowContacts;
