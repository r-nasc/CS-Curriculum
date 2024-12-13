/* eslint-disable react/prop-types */

import { useState } from "react";
import repository from "./ContactRepository";

const AddContact = ({ persons, setPersons, setMessage }) => {
  const [newName, setNewName] = useState("");
  const [newNumber, setNewNumber] = useState("");

  const addContact = (event) => {
    event.preventDefault();

    const confirmUpdate = (name) =>
      window.confirm(
        `${name} is already added to phonebook, replace old number with a new one?`
      );

    for (const p of persons) {
      if (p.name == newName && confirmUpdate(p.name)) {
        repository
          .updateContact(p.id, { number: newNumber })
          .then((contact) => {
            setNewName("");
            setNewNumber("");
            setPersons(persons.map((x) => (x.id === contact.id ? contact : x)));
            setMessage(`Updated ${contact.name}`);
          })
          .catch(() => setMessage("Failed to Update Contact", true));

        return;
      }
    }

    repository
      .createContact(newName, newNumber)
      .then((contact) => {
        setPersons(persons.concat(contact));
        setNewName("");
        setNewNumber("");
        setMessage(`Added ${contact.name}`);
      })
      .catch(() => setMessage("Failed to Add Contact", true));
  };

  const changeName = (event) => setNewName(event.target.value);
  const changeNumber = (event) => setNewNumber(event.target.value);

  return (
    <>
      <h2>Add New</h2>
      <form onSubmit={addContact}>
        <div>
          name: <input value={newName} onChange={changeName} />
        </div>
        <div>
          number: <input value={newNumber} onChange={changeNumber} />
        </div>
        <div>
          <button type="submit">add</button>
        </div>
      </form>
    </>
  );
};

export default AddContact;
