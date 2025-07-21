package br.com.luizgustavosgobi.simpleServer.core.configuration;

import br.com.luizgustavosgobi.simpleServer.logger.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigurationManager {
    private final Properties properties = new Properties();
    private static ConfigurationManager instance;
    private String configFilePath = "server.properties";


    public ConfigurationManager() {
        instance = this;
        load();
    }

    public static synchronized ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }

    /**
     * Carrega as configurações do arquivo padrão
     * @return this para encadeamento de métodos
     */
    public ConfigurationManager load() {
        return load(configFilePath);
    }

    /**
     * Carrega as configurações de um arquivo específico
     * @param filePath caminho do arquivo de configuração
     * @return this para encadeamento de métodos
     */
    public ConfigurationManager load(String filePath) {
        if (filePath != null)
            this.configFilePath = filePath;

        if (Files.exists(Paths.get(configFilePath))) {
            try (InputStream input = new FileInputStream(configFilePath)) {
                properties.load(input);
                Logger.Info("Configurations loaded successfully from: " + configFilePath);
            } catch (IOException e) {
                Logger.Error("Error while trying to read the configuration file: " + e.getMessage());
            }

            return this;
        } else {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();

            try (InputStream input = classloader.getResourceAsStream(configFilePath)) {
                properties.load(input);
                Logger.Info("Configurations loaded successfully from: " + filePath);
            } catch (IOException e) {
                Logger.Error("Error while trying to read the configuration file: " + e.getMessage());
            }
        }

        return this;
    }

    /**
     * Obtém uma configuração como String
     * @param key chave da configuração
     * @return valor da configuração ou null se não existir
     */
    public String getString(String key) {
        return properties.getProperty(key);
    }

    /**
     * Obtém uma configuração como String com valor padrão
     * @param key chave da configuração
     * @param defaultValue valor padrão caso a configuração não exista
     * @return valor da configuração ou o valor padrão
     */
    public String getString(String key, String defaultValue) {
        return properties.getOrDefault(key, defaultValue).toString();
    }

    /**
     * Obtém uma configuração como Integer
     * @param key chave da configuração
     * @param defaultValue valor padrão caso a configuração não exista ou seja inválida
     * @return valor da configuração como Integer
     */
    public Integer getInt(String key, Integer defaultValue) {
        String value = getString(key);
        if (value == null) return defaultValue;

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            Logger.Warn("Configuration is not an integer: " + key + "=" + value + ", applying the default value: " + defaultValue);
            return defaultValue;
        }
    }

    /**
     * Obtém uma configuração como Boolean
     * @param key chave da configuração
     * @param defaultValue valor padrão caso a configuração não exista
     * @return valor da configuração como Boolean
     */
    public Boolean getBoolean(String key, Boolean defaultValue) {
        String value = getString(key);
        if (value == null) return defaultValue;

        return Boolean.parseBoolean(value);
    }

    /**
     * Define uma configuração manualmente
     * @param key chave da configuração
     * @param value valor da configuração
     * @return this para encadeamento de métodos
     */
    public ConfigurationManager set(String key, String value) {
        properties.put(key, value);
        return this;
    }

    /**
     * Verifica se uma configuração existe
     * @param key chave da configuração
     * @return true se a configuração existe
     */
    public boolean hasConfig(String key) {
        return properties.containsKey(key);
    }

    /**
     * Retorna todas as configurações
     * @return mapa com todas as configurações
     */
    public Properties getAllConfigs() {
        return properties;
    }
}
