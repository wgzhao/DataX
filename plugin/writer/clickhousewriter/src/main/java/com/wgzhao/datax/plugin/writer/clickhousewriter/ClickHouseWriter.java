package com.wgzhao.datax.plugin.writer.clickhousewriter;

import com.wgzhao.datax.common.plugin.RecordReceiver;
import com.wgzhao.datax.common.spi.Writer;
import com.wgzhao.datax.common.util.Configuration;
import com.wgzhao.datax.plugin.rdbms.util.DataBaseType;
import com.wgzhao.datax.plugin.rdbms.writer.CommonRdbmsWriter;

import java.util.List;

public class ClickHouseWriter
        extends Writer
{
    private static final DataBaseType DATABASE_TYPE = DataBaseType.ClickHouse;

    public static class Job
            extends Writer.Job
    {
        private Configuration originalConfig = null;
        private CommonRdbmsWriter.Job commonRdbmsWriterMaster;

        @Override
        public void init()
        {
            this.originalConfig = super.getPluginJobConf();
            this.commonRdbmsWriterMaster = new CommonRdbmsWriter.Job(DATABASE_TYPE);
            this.commonRdbmsWriterMaster.init(this.originalConfig);
        }

        @Override
        public void prepare()
        {
            this.commonRdbmsWriterMaster.prepare(this.originalConfig);
        }

        @Override
        public List<Configuration> split(int mandatoryNumber)
        {
            return this.commonRdbmsWriterMaster.split(this.originalConfig, mandatoryNumber);
        }

        @Override
        public void post()
        {
            this.commonRdbmsWriterMaster.post(this.originalConfig);
        }

        @Override
        public void destroy()
        {
            this.commonRdbmsWriterMaster.destroy(this.originalConfig);
        }
    }

    public static class Task
            extends Writer.Task
    {
        private Configuration writerSliceConfig;

        private CommonRdbmsWriter.Task commonRdbmsWriterSlave;

        @Override
        public void init()
        {
            this.writerSliceConfig = super.getPluginJobConf();

            this.commonRdbmsWriterSlave = new CommonRdbmsWriter.Task(DATABASE_TYPE);

            this.commonRdbmsWriterSlave.init(this.writerSliceConfig);
        }

        @Override
        public void prepare()
        {
            this.commonRdbmsWriterSlave.prepare(this.writerSliceConfig);
        }

        @Override
        public void startWrite(RecordReceiver recordReceiver)
        {
            this.commonRdbmsWriterSlave.startWrite(recordReceiver, this.writerSliceConfig, super.getTaskPluginCollector());
        }

        @Override
        public void post()
        {
            this.commonRdbmsWriterSlave.post(this.writerSliceConfig);
        }

        @Override
        public void destroy()
        {
            this.commonRdbmsWriterSlave.destroy(this.writerSliceConfig);
        }
    }
}