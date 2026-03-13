package br.com.luizgustavosgobi.testes;

import br.com.luizgustavosgobi.simpleServer.core.converter.ByteToStringCodec;
import br.com.luizgustavosgobi.simpleServer.core.converter.ConverterPipeline;

//@EnableDataConverters
public class DataConverters {

    public ConverterPipeline pipeline() {
        return new ConverterPipeline()
                .addLast(new ByteToStringCodec());
    }
}
