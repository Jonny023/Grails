package com.base

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import grails.converters.JSON
import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.PNGTranscoder
import org.apache.commons.lang3.StringUtils
import org.flowable.bpmn.converter.BpmnXMLConverter
import org.flowable.bpmn.model.BpmnModel
import org.flowable.bpmn.model.Task
import org.flowable.editor.constants.ModelDataJsonConstants
import org.flowable.editor.language.json.converter.BpmnJsonConverter
import org.flowable.engine.ProcessEngine
import org.flowable.engine.ProcessEngineConfiguration
import org.flowable.engine.ProcessEngines
import org.flowable.engine.repository.Deployment
import org.flowable.engine.repository.Model
import org.flowable.engine.runtime.Execution
import org.flowable.engine.runtime.ProcessInstance
import org.flowable.image.ProcessDiagramGenerator


class FlowController {

    def baseService

    private ObjectMapper objectMapper = new ObjectMapper()

    def index() {

    }

    def list() {

    }

    /**
     *  通过id获取流程
     * @return
     */
    def get() {
        def record = request.JSON ?: params
        ObjectNode modelNode = null
        Model model = baseService.repositoryService.getModel(record.id)
        if (model != null) {
            try {
                if (StringUtils.isNotEmpty(model.getMetaInfo())) {
                    modelNode = (ObjectNode) objectMapper.readTree(model.getMetaInfo())
                } else {
                    modelNode = objectMapper.createObjectNode()
                    modelNode.put("name", model.getName())
                }
                modelNode.put("modelId", model.getId())
                ObjectNode editorJsonNode = (ObjectNode) objectMapper.readTree(new String(baseService.repositoryService.getModelEditorSource(model.getId()), "utf-8"))
                modelNode.put("model", editorJsonNode)

            } catch (Exception e) {
                log.error("not found model info.")
            }
        }
        render modelNode

    }

    /**
     *  保存修改流程
     * @return
     */
    def save() {
        def record = params
        try {
            Model model = baseService.repositoryService.getModel(record.id)
            ObjectNode modelJson = (ObjectNode) objectMapper.readTree(model.getMetaInfo())
            modelJson.put("name", record.name);
            modelJson.put("description", record.description)
            model.setMetaInfo(modelJson.toString())
            model.setName(record.name)
            baseService.repositoryService.saveModel(model)
            baseService.repositoryService.addModelEditorSource(model.getId(), record.json_xml.getBytes("utf-8"))
            InputStream svgStream = new ByteArrayInputStream(record.svg_xml.getBytes("utf-8"))
            TranscoderInput input = new TranscoderInput(svgStream)
            PNGTranscoder transcoder = new PNGTranscoder()
            ByteArrayOutputStream outStream = new ByteArrayOutputStream()
            TranscoderOutput output = new TranscoderOutput(outStream)
            transcoder.transcode(input, output)
            final byte[] result = outStream.toByteArray()
            baseService.repositoryService.addModelEditorSourceExtra(model.getId(), result)
            outStream.close()
        } catch (Exception e) {
            log.error("Error saving model", e)
        }
        render true

    }

    /**
     *  获取汉化菜单
     * @return
     */
//    def getMenu() {
//        InputStream stencilsetStream = this.getClass().getClassLoader().getResourceAsStream("stencilset.json")
//        try {
//            render IOUtils.toString(stencilsetStream, "utf-8")
//        } catch (Exception e) {
//            log.error("Error while loading stencil set", e)
//        }
//    }

    /**
     *  新建一个空模型
     * @return
     */
    def newModel() {

        def record = request.JSON ?: params

        record.name = "new model"
        record.description = "description"

        //初始化一个空模型
        Model model = baseService.repositoryService.newModel()
        //设置一些默认信息
        int revision = 1
        String key = "process"
        ObjectNode modelNode = objectMapper.createObjectNode()
        modelNode.put(ModelDataJsonConstants.MODEL_NAME, record.name)
        modelNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, record.description)
        modelNode.put(ModelDataJsonConstants.MODEL_REVISION, revision)
        model.setName(record.name)
        model.setKey(key)
        model.setMetaInfo(modelNode.toString())
        baseService.repositoryService.saveModel(model)
        String id = model.getId()
        //完善ModelEditorSource
        ObjectNode editorNode = objectMapper.createObjectNode()
        editorNode.put("id", "canvas")
        editorNode.put("resourceId", "canvas")
        ObjectNode stencilSetNode = objectMapper.createObjectNode()
        stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#")
        editorNode.put("stencilset", stencilSetNode)
        baseService.repositoryService.addModelEditorSource(id, editorNode.toString().getBytes("utf-8"))
        def data = [id: ""]
        data.id = id
        render data as JSON
    }

    /**
     *  获取所有模型
     *  page 起始页
     *  limit 每页显示条数
     * @return
     */
    def modelList() {
        def offset = params.page ? (Integer.parseInt(params.page)) : 0
        if (offset > 0) {
            offset = offset - 1
        }
        def pageSize = params.limit ? (Integer.parseInt(params.limit)) : 10
        def datas = baseService.repositoryService.createModelQuery().listPage(offset, pageSize)
        def total = baseService.repositoryService.createModelQuery().count()
        def map = [
                code : 0,
                msg  : "成功",
                count: total,
                data : datas
        ]
        render map as grails.converters.JSON
    }

    /**
     *  发布模型为流程定义
     *  id 流程id
     *
     */
    def deploy() {
        def record = request.JSON ?: params
        //获取模型
        Model modelData = baseService.repositoryService.getModel(record.id)
        byte[] bytes = baseService.repositoryService.getModelEditorSource(modelData.getId())
        if (bytes == null) {
            render "模型数据为空，请先设计流程并成功保存，再进行发布"
        }
        JsonNode modelNode = new ObjectMapper().readTree(bytes)
        BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode)
        if (model.getProcesses().size() == 0) {
            render "数据模型不符要求，请至少设计一条主线流程"
        }
        byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model)
        //发布流程
        String processName = modelData.getName() + ".bpmn20.xml"
        Deployment deployment = baseService.repositoryService.createDeployment()
                .name(modelData.getName())
                .addString(processName, new String(bpmnBytes, "UTF-8"))
                .deploy()
        modelData.setDeploymentId(deployment.getId())
        baseService.repositoryService.saveModel(modelData)
        def map = [result: true]
        render map as JSON
    }

    /**
     *  启动流程
     *  keyName 流程key
     * @return
     */
    def startProcess() {
        def record = request.JSON ?: params
        ProcessInstance process = baseService.runtimeService.startProcessInstanceByKey(record.keyName)
//        render process.getId() + " : " + process.getProcessDefinitionId()
        def map = [result: true]
        render map as JSON
    }

    /**
     *  流程图
     * @return
     */
    def details() {

        def processId = params.processId

        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine()
        ProcessInstance pi = baseService.runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult()

        //流程走完的不显示图
        if (pi == null) {
            render "未查询到记录"
            return
        }

        org.flowable.task.api.Task task = baseService.taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult()
        //使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
        String InstanceId = task.getProcessInstanceId()
        List<Execution> executions = baseService.runtimeService.createExecutionQuery().processInstanceId(InstanceId).list()

        //得到正在执行的Activity的Id
        List<String> activityIds = new ArrayList<>()
        List<String> flows = new ArrayList<>()
        for (Execution exe : executions) {
            List<String> ids = baseService.runtimeService.getActiveActivityIds(exe.getId())
            activityIds.addAll(ids)
        }

        //获取流程图
        BpmnModel bpmnModel = baseService.repositoryService.getBpmnModel(pi.getProcessDefinitionId())
        ProcessEngineConfiguration engconf = processEngine.getProcessEngineConfiguration()
        ProcessDiagramGenerator diagramGenerator = engconf.getProcessDiagramGenerator()


        InputStream inputStream = diagramGenerator.generateDiagram(bpmnModel, "png", activityIds, flows, engconf.getActivityFontName(), engconf.getLabelFontName(), engconf.getAnnotationFontName(), engconf.getClassLoader(), 1.0, false)
        OutputStream out = null
        byte[] buf = new byte[1024]
        int legth = 0
        response.setContentType("image/png")
        response.setHeader("Pragma", "no-cache")
        response.setHeader("Cache-Control", "no-cache")
        try {
            out = response.getOutputStream()
            while ((legth = inputStream.read(buf)) != -1) {
                out.write(buf, 0, legth)
            }
        } finally {
            if (inputStream != null) {
                inputStream.close()
            }
            if (out != null) {
                out.close()
            }
        }
    }
}
