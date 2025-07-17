import React, { useState } from 'react';
import './App.css';
import RoadmapUpload from './components/RoadmapUpload';
import AIBasedRoadmapInput from './components/AIBasedRoadMapInput';
import FlowVisualizer from './components/FlowVisualizer';

function App() {
  const [roadmapData, setRoadmapData] = useState(null);

  return (
    <div className="App">
      <div className="container">
        <h1>AI Roadmap to BPMN Converter</h1>

        <div className="section">
          <h2>Upload Roadmap CSV</h2>
          <RoadmapUpload onUploadSuccess={(data) => setRoadmapData(data)} />
        </div>

        <div className="section green">
          <h2>Generate Roadmap via AI</h2>
          <AIBasedRoadmapInput onUploadSuccess={(data) => setRoadmapData(data)} />
        </div>

        {roadmapData && (
          <div className="section">
            <h2>Visualized BPMN Output</h2>
            <div className="visualizer">
              <FlowVisualizer bpmnXml={roadmapData} />
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

export default App;