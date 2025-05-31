import React from "react";

function Sidebar() {
  return (
    <aside className="w-64 bg-blue-900 text-white p-4">
      <h2 className="text-xl font-bold mb-4">사이드바</h2>
      <ul className="space-y-2">
        <li className="hover:bg-blue-800 p-2 rounded">메뉴1</li>
        <li className="hover:bg-blue-800 p-2 rounded">메뉴2</li>
        <li className="hover:bg-blue-800 p-2 rounded">메뉴3</li>
      </ul>
    </aside>
  );
}

export default Sidebar;
