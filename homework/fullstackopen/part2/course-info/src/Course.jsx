/* eslint-disable react/prop-types */

const Header = ({ name }) => {
  return (
    <>
      <h2>{name}</h2>
    </>
  );
};

const Content = ({ parts }) => {
  return (
    <>
      {parts.map((x) => (
        <p key={x.id}>
          {x.name} {x.exercises}
        </p>
      ))}
      <p>total of {parts.reduce((sum, x) => sum + x.exercises, 0)} exercises</p>
    </>
  );
};

const Course = ({ course }) => {
  return (
    <>
      <Header name={course.name} />
      <Content parts={course.parts} />
    </>
  );
};

export default Course;
