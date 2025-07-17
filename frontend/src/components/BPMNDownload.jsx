import React from "react";

const BPMNDownload = ({ bpmnXml }) => {
  const downloadBPMN = () => {
    const blob = new Blob([bpmnXml], { type: "application/xml" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = "workflow.bpmn";
    a.click();
    URL.revokeObjectURL(url);
  };

  return (
    <div style={{ marginTop: "1rem" }}>
      <button onClick={downloadBPMN}>Download BPMN</button>
    </div>
  );
};

export default BPMNDownload;