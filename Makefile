.PHONY: help run build test clean install clean-build

# Default target
.DEFAULT_GOAL := help

# Variables
GRADLE := ./gradlew
JAVA_VERSION := 17

help: ## Show this help message
	@echo "Available commands:"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}'

run: ## Run the Spring Boot application
	$(GRADLE) bootRun

build: ## Build the project (includes tests)
	$(GRADLE) build

build-fast: ## Build the project without running tests (faster)
	$(GRADLE) build -x test

test: ## Run all tests
	$(GRADLE) test

clean: ## Clean build directory
	$(GRADLE) clean

clean-build: clean build-fast ## Clean and build without tests

install: build ## Build and install dependencies
	@echo "Dependencies installed"

jar: build ## Build executable JAR file
	$(GRADLE) bootJar
	@echo "JAR file created in build/libs/"

stop: ## Stop running Spring Boot application
	@pkill -f "gradlew bootRun" || echo "No running application found"

status: ## Check application status
	@lsof -i :8080 || echo "Port 8080 is not in use"

