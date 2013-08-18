/**
 * 
 */
package com.terrapin.emwin.storm;

import java.io.Serializable;
import java.util.List;

import backtype.storm.tuple.Fields;

import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;
import com.mapr.ProtoSpout.TupleParser;

/**
 * @author pcurtis
 *
 */
public class BlobTupleParser extends TupleParser implements Serializable {

    /* (non-Javadoc)
     * @see com.mapr.ProtoSpout.TupleParser#parse(com.google.protobuf.ByteString)
     */
    @Override
    public List<Object> parse(ByteString buffer) {
        return Lists.<Object> newArrayList(buffer.toByteArray());
    }

    /* (non-Javadoc)
     * @see com.mapr.ProtoSpout.TupleParser#getOutputFields()
     */
    @Override
    public Fields getOutputFields() {
        return new Fields("msg");    
    }

}
