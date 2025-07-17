import React, { useEffect, useRef } from "react";
import BpmnViewer from "bpmn-js/lib/Viewer";

const FlowVisualizer = ({ bpmnXml }) => {
  const bpmnContainer = useRef(null);
  const viewerRef = useRef(null);

  useEffect(() => {
    if (bpmnXml && bpmnContainer.current) {
      console.log(bpmnXml);
      if (viewerRef.current) {
        viewerRef.current.destroy();
      }

      const viewer = new BpmnViewer({
        container: bpmnContainer.current
      });

      viewer.importXML(bpmnXml)
        .then(() => {
          viewer.get("canvas").zoom("fit-viewport");
        })
        .catch((err) => {
          console.error("Error rendering BPMN diagram:", err);
        });

      viewerRef.current = viewer;
    }
  }, [bpmnXml]);

  return (
    <div>
      <h3>BPMN Preview</h3>
      <div
        ref={bpmnContainer}
        style={{ border: "1px solid #ccc", height: "500px", width: "100%" }}
      />
    </div>
  );
};

export default FlowVisualizer;