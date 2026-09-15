use std::process::{Child, Command};
use std::sync::{Arc, Mutex};
use std::path::PathBuf;

pub struct SidecarManager {
    child: Arc<Mutex<Option<Child>>>,
}

impl SidecarManager {
    pub fn new() -> Self {
        Self {
            child: Arc::new(Mutex::new(None)),
        }
    }

    pub fn start_sidecar(&self, jar_path: PathBuf) -> Result<(), String> {
        let mut child_guard = self.child.lock().map_err(|e| e.to_string())?;

        if child_guard.is_some() {
            return Ok(()); // Already running
        }

        println!("Starting PaperForge Spring Boot Sidecar on port 8081...");
        println!("Backend JAR Path: {:?}", jar_path);

        let child = Command::new("java")
            .arg("-Djava.security.egd=file:/dev/./urandom")
            .arg("-Dfile.encoding=UTF-8")
            .arg("-jar")
            .arg(jar_path)
            .arg("--server.port=8081")
            .arg("--PAPERFORGE_STORAGE_PATH=/tmp/paperforge-desktop-storage")
            .spawn()
            .map_err(|e| format!("Failed to launch Java Spring Boot sidecar process: {}. Ensure Java 21+ is installed.", e))?;

        *child_guard = Some(child);
        println!("PaperForge Sidecar launched successfully on port 8081.");

        Ok(())
    }

    pub fn stop_sidecar(&self) {
        if let Ok(mut child_guard) = self.child.lock() {
            if let Some(mut child) = child_guard.take() {
                println!("Stopping PaperForge Sidecar process...");
                let _ = child.kill();
                let _ = child.wait();
                println!("PaperForge Sidecar stopped cleanly.");
            }
        }
    }

    pub fn is_running(&self) -> bool {
        if let Ok(mut child_guard) = self.child.lock() {
            if let Some(ref mut child) = *child_guard {
                match child.try_wait() {
                    Ok(None) => true,
                    _ => false,
                }
            } else {
                false
            }
        } else {
            false
        }
    }
}
