import React, { useState } from 'react';
import axios from 'axios';

const AIBasedRoadmapInput = ({ onUploadSuccess }) => {
  const [text, setText] = useState('');
  const [status, setStatus] = useState('');

  const handleMapClick = async () => {
    if (!text.trim()) {
      setStatus('Please enter some roadmap text.');
      return;
    }

    try {
      setStatus('Mapping via AI...');
      const response = await axios.post(
        'http://localhost:8080/api/roadmap/map-ai',
        text,
        { headers: { 'Content-Type': 'text/plain' } }
      );

      console.log('Mapped Roadmap:', response.data);
      onUploadSuccess(response.data); // same as file upload handler
      setStatus('Mapped successfully!');
    } catch (error) {
      console.error('Mapping failed:', error);
      setStatus('Mapping failed. Check server.');
    }
  };

  return (
    <div className="ai-input-container">
      <h2>OR Paste Raw Roadmap Text</h2>
      <textarea
        rows="6"
        placeholder="e.g. User signs up → receives email → completes profile → sees dashboard"
        value={text}
        onChange={(e) => setText(e.target.value)}
        style={{ width: '100%' }}
      />
      <button onClick={handleMapClick}>Map to BPMN via AI</button>
      <p>{status}</p>
    </div>
  );
};

export default AIBasedRoadmapInput;