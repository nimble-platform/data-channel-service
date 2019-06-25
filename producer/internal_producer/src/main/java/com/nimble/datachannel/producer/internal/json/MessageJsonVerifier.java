/*
This will be the future json verifier in order to decide if a produced message is compliant to a json schema.
for example it will be usefull to configure Product Lifecycle Management (PLM)
*/


package com.nimble.datachannel.producer.internal.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jackson.JsonLoader;

import java.io.IOException;

public final class MessageJsonVerifier
{
    public static void main(final String... args)
        throws IOException, ProcessingException
    {
        final JsonNode fstabSchema = JsonLoader.fromResource("/json/fstab.json");
        final JsonNode good = JsonLoader.fromResource("/json/fstab-good.json");
        final JsonNode bad = JsonLoader.fromResource("/json/fstab-bad.json");
        final JsonNode bad2 = JsonLoader.fromResource("/json/fstab-bad2.json");

        final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();

        final JsonSchema schema = factory.getJsonSchema(fstabSchema);

        ProcessingReport report;

        report = schema.validate(good);
        System.out.println(report);

        report = schema.validate(bad);
        System.out.println(report);

        report = schema.validate(bad2);
        System.out.println(report);
    }
}
