package sg.jackiez.worker.module.ok.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

import sg.jackiez.worker.module.ok.model.base.BaseM;
import sg.jackiez.worker.utils.DateUtil;

public class ServerTime extends BaseM {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtil.ISO_FORMAT)
    public Date iso;

    public double epoch;
}
