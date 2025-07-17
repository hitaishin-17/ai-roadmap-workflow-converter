import React, { useState } from 'react';
import axios from 'axios';

const RoadmapUpload = () => {
  const [file, setFile] = useState(null);
  const [status, setStatus] = useState('');

  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
    setStatus('');
  };

  const handleUpload = async () => {
    if (!file) {
      setStatus('Please select a file first.');
      return;
    }

    const formData = new FormData();
    formData.append('file', file);

    try {
      setStatus('Uploading...');
      const response = await axios.post('http://localhost:8080/api/roadmap/convert', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
        responseType: 'blob', // 👈 important for file
      });
      
      const url = window.URL.createObjectURL(new Blob([response.data]));
      console.log('BPMN file URL:', url);
      console.log('BPMN file response:', response.data);
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', 'workflow.bpmn'); // 👈 same as backend filename
      document.body.appendChild(link);
      link.click();

      setStatus('✅ BPMN file downloaded!');
    } catch (error) {
      console.error('❌ Upload failed:', error);
      setStatus('Upload failed. Please check the server.');
    }
  };

  return (
    <div className="upload-container">
      <h2>Upload Roadmap File</h2>
      <input type="file" accept=".csv" onChange={handleFileChange} />
      <button onClick={handleUpload}>Convert to BPMN</button>
      <p>{status}</p>
    </div>
  );
};

export default RoadmapUpload;