import React, { useEffect, useState } from 'react';
import { Todo } from '../../model/model';

export default function TodoMain() {
  const [todoList, setTodoList] = useState<Array<Todo>>([
    {
      id: 1,
      message: 'Hello, World',
      atSchedule: new Date('2025-08-01'),
    },
    { id: 2, message: 'Hello, World', atSchedule: new Date('2025-08-01') },
    { id: 3, message: 'Hello, World', atSchedule: new Date('2025-08-01') },
    { id: 4, message: 'Hello, World', atSchedule: new Date('2025-08-01') },
    { id: 5, message: 'Hello, World', atSchedule: new Date('2025-08-01') },
  ]);

  useEffect(() => {
    // 최초 할일리스트 요청
  }, []);

  const clickRemoveButton = (event: React.MouseEvent) => {
    event.preventDefault();
    // 클릭 시 해당 할일목록 제거
  };

  return (
    <div className="container">
      <h2 className="title">할 일 목록</h2>
      <div className="item-list">
        {todoList.map((val, index) => (
          <div className="item">
            <p>{val.message}</p>
            <p>{String(val.atSchedule)}</p>
            <button onClick={clickRemoveButton}>삭제</button>
          </div>
        ))}
      </div>
    </div>
  );
}
