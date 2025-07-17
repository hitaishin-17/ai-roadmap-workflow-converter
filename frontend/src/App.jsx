import React, { useState } from 'react';
import RoadmapUpload from './components/RoadmapUpload';
import AIBasedRoadmapInput from './components/AIBasedRoadMapInput';
import FlowVisualizer from './components/FlowVisualizer';

function App() {
  const [roadmapData, setRoadmapData] = useState(null);

  return (
    <div className="App">
      <h1>AI Roadmap to BPMN Converter</h1>

      <RoadmapUpload onUploadSuccess={(data) => setRoadmapData(data)} />
      
      <hr />
      <AIBasedRoadmapInput onUploadSuccess={(data) => setRoadmapData(data)} />

      {roadmapData && (
      <FlowVisualizer bpmnXml={roadmapData} />
    )}
    </div>
  );
}

export default App;