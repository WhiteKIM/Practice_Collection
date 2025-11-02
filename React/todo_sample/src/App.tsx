import logo from './logo.svg';
import './App.css';
import React from 'react';
import TodoMain from './page/todo/TodoMain';

const App: React.FC = () => {
  return (
    <div className="App">
      <TodoMain />
    </div>
  );
};

export default App;
