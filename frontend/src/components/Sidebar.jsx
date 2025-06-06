import React from 'react';

function Sidebar() {
  return (
    <aside className="w-60 bg-blue-800 text-white p-4 h-full overflow-y-auto">
      <h2 className="text-lg font-semibold mb-4">Sidebar</h2>
      <ul>
        <li className="mb-2 hover:underline cursor-pointer">Consultations</li>
        <li className="mb-2 hover:underline cursor-pointer">Evaluation</li>
        <li className="mb-2 hover:underline cursor-pointer">Upload</li>
      </ul>
    </aside>
  );
}

export default Sidebar;
