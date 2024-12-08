/* eslint-disable react/prop-types */
import { useState } from "react";

function Button({ onClick, label }) {
  return <button onClick={onClick}>{label}</button>;
}

function StatisticLine({ text, value }) {
  return (
    <tr>
      <td>{text}</td>
      <td>{value}</td>
    </tr>
  );
}

function Statistics({ good, neutral, bad }) {
  const total = good + neutral + bad;
  if (total === 0) {
    return (
      <>
        <h1>statistics</h1>
        <p>No feedback given</p>
      </>
    );
  }
  return (
    <>
      <h1>statistics</h1>
      <table>
        <tbody>
          <StatisticLine text="good" value={good} />
          <StatisticLine text="neutral" value={neutral} />
          <StatisticLine text="bad" value={bad} />
          <StatisticLine text="all" value={total} />
          <StatisticLine text="average" value={(good - bad) / total} />
          <StatisticLine text="positive" value={(good / total) * 100 + "%"} />
        </tbody>
      </table>
    </>
  );
}

function App() {
  const [good, setGood] = useState(0);
  const [bad, setBad] = useState(0);
  const [neutral, setNeutral] = useState(0);

  return (
    <>
      <div>
        <h1>give feedback</h1>
        <Button onClick={() => setGood(good + 1)} label="good" />
        <Button onClick={() => setNeutral(neutral + 1)} label="neutral" />
        <Button onClick={() => setBad(bad + 1)} label="bad" />
        <Statistics good={good} neutral={neutral} bad={bad} />
      </div>
    </>
  );
}

export default App;
