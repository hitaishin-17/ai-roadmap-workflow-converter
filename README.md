#  AI Roadmap → BPMN Workflow Converter

Convert roadmap data into clean, executable BPMN workflows using AI.
Built with Java + React + Mistral (via Ollama), this tool demonstrates how AI can transform unstructured text into structured BPMN 2.0 XML ready for workflow engines like Camunda 
---

## Overview

This system bridges unstructured planning data and process execution pipelines.
It automatically translates roadmap steps (CSV or plain text) into BPMN diagrams that can be directly imported into workflow automation engines.
### Core Capabilities
	•	AI-driven text-to-BPMN conversion
	•	Self-hosted Mistral LLM for offline processing
	•	Java Spring Boot backend generating BPMN XML
	•	React + bpmn-js frontend for live visualization
	•	Export support for BPMN XML and SVG

---
## Architecture
## Tech Stack
| Technology       | Role |
|------------------|------|
| **Frontend**     | React, bpmn-js – Upload, preview, and export BPMN diagrams |
| **Backend**      | Java + Spring Boot – Parses input, calls LLM, generates BPMN XML |
| **LLM Layer**    | Mistral via Ollama – Converts roadmap steps into structured process logic |
| **Visualization**| bpmn-js – Renders BPMN 2.0 diagrams in real-time |
| **Output**       | BPMN 2.0 XML – Compatible with Camunda, Zeebe, and other engines |


## 🔍 Features

	•	Accepts CSV or text roadmap input
	•	Generates valid BPMN 2.0 XML using AI
	•	Renders workflow visually in-browser
	•	Exports directly to Camunda/Zeebe engines
	•	Modular architecture for custom AI models
	•	Lightweight, container-ready deployment

---

## Demo

Here’s how the tool works in action:
Upload a roadmap CSV — for example:
1. Idea Review  
2. Design Kickoff  
3. Development  
4. QA Testing  
5. Launch  

The backend sends each step to Mistral → receives structured XML → visualized instantly.

![bpmn demo](./diagrams/screenshot.png)

📽️ [Watch the Demo Video](https://drive.google.com/file/d/1faDeZ9HTd4mtFPzQUYmWRXk0b9qaOZdW/view?usp=sharing)
---

## Getting Started
Prerequisites
- Java 17+
- Node.js 18+
- Ollama with Mistral model installed
Run locally
# Start Ollama + Mistral
```bash
ollama run mistral

# Backend
cd backend
./mvnw spring-boot:run

# Frontend
cd frontend
npm install
npm start
```
---

**Performance & Results**
	- Reduced BPMN creation time from 2 hrs → 30 secs
	- Successfully converted 10+ sample roadmaps
	- Generated consistent BPMN-compliant XML output validated via Camunda Modeler

**Future Enhancements**

	- Gateway and decision node recognition
	- Streaming LLM output for real-time rendering
	- Export as SVG/PNG for reports
	- Multi-language roadmap parsing
	- Slack/Notion roadmap ingestion APIs
	- Audit logging for enterprise deployments

**About This Build**

I’m Hitaishi N, a backend engineer focused on AI-driven workflow automation and system orchestration.
This project demonstrates how LLMs can generate structured BPMN process flows programmatically — bridging AI inference with executable workflow standards.
The emphasis is on modularity, reproducibility, and interoperability across workflow engines.

📩 [Let’s connect on LinkedIn](https://www.linkedin.com/in/hitaishi-n-grovista)


