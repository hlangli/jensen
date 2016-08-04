package dk.langli.jensen;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter.Indenter;

public class DefaultPrettyPrinter implements PrettyPrinter {
    private com.fasterxml.jackson.core.util.DefaultPrettyPrinter prettyPrinter = null;
    
    public DefaultPrettyPrinter() {
        this("	");
    }
    
    public DefaultPrettyPrinter(String indentation) {
        prettyPrinter = new com.fasterxml.jackson.core.util.DefaultPrettyPrinter();
        Indenter indenter = new DefaultIndenter(indentation, DefaultIndenter.SYS_LF);
        prettyPrinter.indentObjectsWith(indenter);
    }

    @Override
    public void writeRootValueSeparator(JsonGenerator jg) throws IOException, JsonGenerationException {
        prettyPrinter.writeArrayValueSeparator(jg);
    }

    @Override
    public void writeStartObject(JsonGenerator gen) throws IOException, JsonGenerationException {
        prettyPrinter.writeStartObject(gen);
    }

    @Override
    public void writeEndObject(JsonGenerator gen, int nrOfEntries) throws IOException, JsonGenerationException {
        prettyPrinter.writeEndObject(gen, nrOfEntries);
    }

    @Override
    public void writeObjectEntrySeparator(JsonGenerator gen) throws IOException, JsonGenerationException {
        prettyPrinter.writeObjectEntrySeparator(gen);
    }

    @Override
    public void writeObjectFieldValueSeparator(JsonGenerator gen) throws IOException, JsonGenerationException {
        prettyPrinter.writeObjectFieldValueSeparator(gen);
    }

    @Override
    public void writeStartArray(JsonGenerator gen) throws IOException, JsonGenerationException {
        prettyPrinter.writeStartArray(gen);
    }

    @Override
    public void writeEndArray(JsonGenerator gen, int nrOfValues) throws IOException, JsonGenerationException {
        prettyPrinter.writeEndArray(gen, nrOfValues);
    }

    @Override
    public void writeArrayValueSeparator(JsonGenerator gen) throws IOException, JsonGenerationException {
        prettyPrinter.writeArrayValueSeparator(gen);
    }

    @Override
    public void beforeArrayValues(JsonGenerator gen) throws IOException, JsonGenerationException {
        prettyPrinter.beforeArrayValues(gen);
    }

    @Override
    public void beforeObjectEntries(JsonGenerator gen) throws IOException, JsonGenerationException {
        prettyPrinter.beforeObjectEntries(gen);
    }
}
