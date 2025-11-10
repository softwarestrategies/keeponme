.PHONY: clean verify

# Clean all project Docker resources
clean:
	@echo "🧹 Cleaning project Docker resources..."
	docker compose down -v
	docker volume rm keeponme_postgres_data 2>/dev/null || true
	docker rm -f keeponme-postgres keeponme-keycloak 2>/dev/null || true
	@echo "✅ Project cleaned!"

# Verify cleanup
verify:
	@echo "📊 Checking for remaining project resources..."
	@echo ""
	@echo "Containers:"
	@docker ps -a | grep keeponme || echo "  ✅ No project containers found"
	@echo ""
	@echo "Volumes:"
	@docker volume ls | grep keeponme || echo "  ✅ No project volumes found"
	@echo ""
	@echo "Networks:"
	@docker network ls | grep keeponme || echo "  ✅ No project networks found"
