"use strict";(self["webpackChunkstatic"]=self["webpackChunkstatic"]||[]).push([[966],{9047:function(e,l,a){a.d(l,{Ug:function(){return i},ol:function(){return s},sO:function(){return t}});var u=a(7327);class i{constructor(e,l){(0,u.Z)(this,"dictOption",void 0),(0,u.Z)(this,"dictKey",void 0),this.dictOption=e,this.dictKey=l}}class t{constructor(e,l){(0,u.Z)(this,"key",void 0),(0,u.Z)(this,"value",void 0),this.key=e,this.value=l}invalid(){return void 0==this.key||null==this.key||""==this.key||void 0==this.value||null==this.value||""==this.value}}class s{constructor(e,l,a,i,t){(0,u.Z)(this,"fieldName",void 0),(0,u.Z)(this,"fieldDesc",void 0),(0,u.Z)(this,"enumType",void 0),(0,u.Z)(this,"enumSql",void 0),(0,u.Z)(this,"pairs",void 0),this.fieldName=e,this.fieldDesc=l,this.enumType=a,this.enumSql=i,this.pairs=t}copyWithRouteQuery(e){this.fieldName=e.fieldName,this.fieldDesc=e.fieldDesc,this.enumType=e.enumType,this.enumSql=e.enumSql,this.pairs=[]}isSqlEnum(){return"sql"==this.enumType}isHandEnum(){return"enum"==this.enumType}}},89:function(e,l){l.Z=(e,l)=>{const a=e.__vccOpts||e;for(const[u,i]of l)a[u]=i;return a}},1966:function(e,l,a){a.r(l),a.d(l,{default:function(){return q}});a(7658);var u=a(3396),i=a(4870),t=a(9242),s=a(1685),n=a(2748),o=a(4834),d=a(3824),r=a(9047);const m=e=>((0,u.dD)("data-v-608e9952"),e=e(),(0,u.Cn)(),e),c={class:"enums"},p=m((()=>(0,u._)("span",{class:"pair-key"},"键：",-1))),f=m((()=>(0,u._)("span",{class:"pair-value"},"值：",-1)));var h=(0,u.aZ)({__name:"DictInfo",setup(e){const l=(0,i.iH)("default"),a=(0,i.iH)(6),m=(0,i.iH)(!1),h=(0,i.iH)(),v=(0,i.qj)(new r.ol("","","","",[])),y=(0,i.qj)({fieldName:[{required:!0,message:"字典名称不能为空",trigger:"blur"}],fieldDesc:[{required:!0,message:"字典描述不能为空",trigger:"blur"}],enumType:[{required:!0,message:"枚举类型必须要选择一个",trigger:"blur"}],enumSql:[{validator:(e,l,a)=>{"sql"==v.enumType&&""==l&&a(new Error("枚举sql不能为空")),a()},trigger:"blur"}]}),q=(0,i.qj)([{value:"enum",label:"手工枚举"},{value:"sql",label:"从sql查询"}]),w=()=>{const e=new r.sO("","");v.pairs.push(e)},g=e=>{console.log("删除枚举"),v.pairs.splice(e,1)},b=()=>{console.log("已经添加的枚举值: ",v.pairs)},_=async e=>{console.log("提交的表单: ",v),e&&await e.validate(((e,l)=>{e?(0,s.Z)({url:"/hmis/manage/dict/saveDictInfo/v1.0",method:"post",data:v}).then((e=>{1==e.data.code&&((0,o.WI)("保存成功","success"),d.Z.push("/dict/list"))})).catch((e=>{(0,o.WI)("保存失败","error")})):console.log("表单校验错误: ",l)}))};return(0,u.bv)((()=>{const e=d.Z.currentRoute.value.query;(0,o.VH)(e)||(console.log("编辑参数：",e),v.copyWithRouteQuery(e),m.value=!0,v.isSqlEnum()&&(0,s.Z)({url:"/hmis/manage/dict/queryDictSqlExt/v1.0",params:{fn:v.fieldName}}).then((e=>{1==e.data.code&&(v.enumSql=e.data.body.enumSql)})),v.isHandEnum()&&(0,s.Z)({url:"/hmis/manage/dict/queryDictEnumExt/v1.0",params:{fn:v.fieldName}}).then((e=>{1==e.data.code&&(v.pairs=e.data.body.pairs)})))})),(e,s)=>{const o=(0,u.up)("el-input"),d=(0,u.up)("el-form-item"),r=(0,u.up)("el-option"),V=(0,u.up)("el-select"),W=(0,u.up)("el-icon"),k=(0,u.up)("el-button"),Z=(0,u.up)("el-form");return(0,u.wg)(),(0,u.j4)(Z,{inline:!1,class:"demo-form-inline","label-width":"120px",ref_key:"ruleFormRef",ref:h,model:v,rules:y,size:l.value,"status-icon":""},{default:(0,u.w5)((()=>[(0,u.Wm)(d,{label:"字段名称",prop:"fieldName"},{default:(0,u.w5)((()=>[(0,u.Wm)(o,{modelValue:v.fieldName,"onUpdate:modelValue":s[0]||(s[0]=e=>v.fieldName=e),placeholder:"请输入字段名称...",disabled:m.value},null,8,["modelValue","disabled"])])),_:1}),(0,u.Wm)(d,{label:"字段描述",prop:"fieldDesc"},{default:(0,u.w5)((()=>[(0,u.Wm)(o,{modelValue:v.fieldDesc,"onUpdate:modelValue":s[1]||(s[1]=e=>v.fieldDesc=e),placeholder:"请输入字段描述..."},null,8,["modelValue"])])),_:1}),(0,u.Wm)(d,{label:"枚举来源",prop:"enumType"},{default:(0,u.w5)((()=>[(0,u.Wm)(V,{modelValue:v.enumType,"onUpdate:modelValue":s[2]||(s[2]=e=>v.enumType=e),class:"m-2",placeholder:"请选择",size:"large"},{default:(0,u.w5)((()=>[((0,u.wg)(!0),(0,u.iD)(u.HY,null,(0,u.Ko)(q,((e,l)=>((0,u.wg)(),(0,u.j4)(r,{key:e.value,label:e.label,value:e.value},null,8,["label","value"])))),128))])),_:1},8,["modelValue"])])),_:1}),(0,u.wy)((0,u.Wm)(d,{label:"添加枚举"},{default:(0,u.w5)((()=>[(0,u.Wm)(W,{size:20,class:"icon-circle-plus-filled",onClick:w},{default:(0,u.w5)((()=>[(0,u.Wm)((0,i.SU)(n.aZ7))])),_:1})])),_:1},512),[[t.F8,"enum"==v.enumType]]),((0,u.wg)(!0),(0,u.iD)(u.HY,null,(0,u.Ko)(v.pairs,((e,l)=>(0,u.wy)(((0,u.wg)(),(0,u.j4)(d,{label:""},{default:(0,u.w5)((()=>[(0,u._)("div",c,[p,(0,u.Wm)(o,{modelValue:e.key,"onUpdate:modelValue":l=>e.key=l,onBlur:b},null,8,["modelValue","onUpdate:modelValue"]),f,(0,u.Wm)(o,{modelValue:e.value,"onUpdate:modelValue":l=>e.value=l,onBlur:b},null,8,["modelValue","onUpdate:modelValue"]),(0,u.Wm)(W,{size:20,class:"icon-delete",onClick:e=>g(l)},{default:(0,u.w5)((()=>[(0,u.Wm)((0,i.SU)(n.HG3))])),_:2},1032,["onClick"])])])),_:2},1536)),[[t.F8,"enum"==v.enumType]]))),256)),(0,u.wy)((0,u.Wm)(d,{label:"来源sql",prop:"enumSql"},{default:(0,u.w5)((()=>[(0,u.Wm)(o,{modelValue:v.enumSql,"onUpdate:modelValue":s[3]||(s[3]=e=>v.enumSql=e),placeholder:"请输入枚举sql...",type:"textarea",rows:a.value},null,8,["modelValue","rows"])])),_:1},512),[[t.F8,"sql"==v.enumType]]),(0,u.Wm)(d,null,{default:(0,u.w5)((()=>[(0,u.Wm)(k,{type:"primary",onClick:s[4]||(s[4]=e=>_(h.value))},{default:(0,u.w5)((()=>[(0,u.Uk)("保存")])),_:1})])),_:1})])),_:1},8,["model","rules","size"])}}}),v=a(89);const y=(0,v.Z)(h,[["__scopeId","data-v-608e9952"]]);var q=y}}]);
//# sourceMappingURL=966.2d45fdfa.js.map