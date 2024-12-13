/* eslint-disable react/prop-types */

const Notification = (message, style) => {
  if (message === null || message.length === 0) {
    return null;
  }

  return <div style={style}>{message}</div>;
};

const SuccessMessage = ({ message }) => {
  const style = {
    color: "green",
    background: "lightgreen",
    fontSize: "20px",
    borderStyle: "solid",
    borderRadius: "5px",
    padding: "10px",
    marginBottom: "10px",
  };
  return Notification(message, style);
};

const ErrorMessage = ({ message }) => {
  const style = {
    color: "red",
    background: "lightgrey",
    fontSize: "20px",
    borderStyle: "solid",
    borderRadius: "5px",
    padding: "10px",
    marginBottom: "10px",
  };
  return Notification(message, style);
};

export default { SuccessMessage, ErrorMessage };
