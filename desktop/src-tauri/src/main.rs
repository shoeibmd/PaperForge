// Prevents additional console window on Windows in release
#![cfg_attr(not(debug_assertions), windows_subsystem = "windows")]

mod sidecar;

use sidecar::SidecarManager;
use std::path::PathBuf;
use tauri::{State, Manager, AppHandle};
use tauri_plugin_dialog::DialogExt;

struct AppState {
    sidecar_manager: SidecarManager,
}

#[tauri::command]
fn get_sidecar_status(state: State<AppState>) -> Result<bool, String> {
    Ok(state.sidecar_manager.is_running())
}

#[tauri::command]
async fn open_file_dialog(app: AppHandle) -> Result<Option<String>, String> {
    let file_path = app.dialog().file().blocking_pick_file();
    Ok(file_path.map(|p| p.to_string()))
}

fn resolve_jar_path(app: &AppHandle) -> PathBuf {
    // 1. Try resolving via Tauri production resource dir
    if let Ok(resource_dir) = app.path().resource_dir() {
        let resource_jar = resource_dir.join("paperforge-backend-0.0.1-SNAPSHOT.jar");
        if resource_jar.exists() {
            return resource_jar;
        }
    }

    // 2. Fallback to local development path relative to repo root
    let dev_jar = std::env::current_dir()
        .unwrap_or_else(|_| PathBuf::from("."))
        .join("backend/target/paperforge-backend-0.0.1-SNAPSHOT.jar");

    dev_jar
}

fn main() {
    let sidecar_manager = SidecarManager::new();
    let state = AppState {
        sidecar_manager,
    };

    tauri::Builder::default()
        .plugin(tauri_plugin_dialog::init())
        .plugin(tauri_plugin_shell::init())
        .manage(state)
        .invoke_handler(tauri::generate_handler![
            get_sidecar_status,
            open_file_dialog
        ])
        .setup(|app| {
            let app_handle = app.handle();
            let jar_path = resolve_jar_path(app_handle);
            let state = app_handle.state::<AppState>();

            if jar_path.exists() {
                let _ = state.sidecar_manager.start_sidecar(jar_path);
            } else {
                eprintln!("Backend JAR not found at {:?}. Sidecar will start when backend is built.", jar_path);
            }

            Ok(())
        })
        .on_window_event(|window, event| {
            if let tauri::WindowEvent::CloseRequested { .. } = event {
                let state = window.state::<AppState>();
                state.sidecar_manager.stop_sidecar();
            }
        })
        .run(tauri::generate_context!())
        .expect("error while running PaperForge Desktop application");
}
